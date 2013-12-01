<?php

class User_model extends MY_Model {

    var $id;
    var $mail;
    var $name;
    var $birthdate;
    var $surname;
    var $active;
    var $gender;
    var $activationdate;
    var $password;

    public function __construct() {
        parent::__construct();
    }

    public function to_string() {
        if (empty($this->name) && empty($this->surname))
            return $this->mail;

        return $this->name . " " . $this->surname;
    }

    public function get_user_by_key($key) {
        $this->db->select('a.*')
                ->join("validationkeys b", "a.id=b.userid")
                ->from("users a")
                ->where("b.validationkey", $key);
        $query = $this->db->get();
        if ($query->num_rows() === 1) {
            $this->assign($query->row());
            return TRUE;
        }
        return FALSE;
    }

    public function get_user($mail) {
        $query = $this->db->get_where('users ', array('mail' => $mail));
        if ($query->num_rows() === 1) {
            $this->assign($query->row());
            return TRUE;
        }
        return FALSE;
    }

    public function get_linked_users() {
        $query = $this->db
                ->select('mail, name, birthdate, surname, gender')
                ->distinct()
                ->where('(linkedusers.userid1=' . $this->id . ' or linkedusers.userid2= ' . $this->id . ') and active = 1 and id<> ' . $this->id)
                ->join('users', 'users.id = linkedusers.userid1 or users.id = linkedusers.userid2')
                ->get('linkedusers');

        $result = $query->result_array();
        foreach ($result as &$value) {
            $value = (object) $value;
        }

        return $result;
    }

    public function get_not_linked_users($userid, $filter, $beginning) {
        $w = 'id not in (select userid1 from linkedusers where userid2 = ' 
            . $this->id . ' union select userid2 from linkedusers where userid1 = ' 
                . $this->id 
                . ') and active = 1 and id <> '
                . $this->id . ' and concat(name, " ", surname) like "'
                . ($beginning ? '' : '%')
                . $filter . '%'
                . '"';
       $query = $this->db
                ->select('id, name, surname')
                ->distinct()
                ->where($w)
                ->get('users', 10);

        return $query->result_array();
    }

    public function activate_user() {
        $this->active = TRUE;
        $this->activationdate = date('Y-m-d');
        $this->load->library('Crypter');
        $pwd = $this->crypter->decrypt($this->input->post('password'));
        $this->password = $pwd;
        $this->db->where('id', $this->id);
        $this->db->update('users', $this);
    }

    public function create_user() {
        $this->mail = $this->input->post('mail');
        $this->name = $this->input->post('name');
        $this->surname = $this->input->post('surname');
        $this->active = FALSE;
        $this->db->insert('users', $this);
        $this->id = $this->db->insert_id();
    }

    public function update_user($data) {
        $this->db->where('id', $this->id);
        return $this->db->update('users', $data);
    }

    public function save_email($mail) {
        $this->mail = $mail;

        $this->db->insert('users', $this);
    }

}
