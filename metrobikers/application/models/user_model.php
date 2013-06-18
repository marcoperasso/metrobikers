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

    public function __construct() {
        parent::__construct();
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
        $this->birthdate = date('Y-m-d', strtotime($this->input->post('birthdate')));
        $this->surname = $this->input->post('surname');
        $this->active = FALSE;
        $this->gender = $this->input->post('gender');

        $this->db->insert('users', $this);

        $this->db->select('id')->where('mail', $this->mail);
        $query = $this->db->get('users');
        $this->id = $query->row()->id;
    }

}