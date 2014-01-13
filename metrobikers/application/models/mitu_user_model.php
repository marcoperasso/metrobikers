<?php

class MITU_User_model extends MY_Model {

    var $id;
    var $mail;
    var $userid;
    var $name;
    var $surname;
    var $password;

    public function __construct() {
        parent::__construct();
    }

    public function create_user() {
        $this->db->insert('mitu_users', $this);
        $this->id = $this->db->insert_id();
    }

    public function update_user() {
        $this->db->where('id', $this->id);
        return $this->db->update('mitu_users', array(
                    'mail' => $this->mail,
                    'userid' => $this->userid,
                    'name' => $this->name,
                    'surname' => $this->surname,
                    'password' => $this->password
        ));
    }

    public function get_user($userid) {
        $query = $this->db->get_where('mitu_users ', array('userid' => $userid));
        if ($query->num_rows() === 1) {
            $this->assign($query->row());
            return TRUE;
        }
        return FALSE;
    }

    public function get_users($filter) {
        $this->db->select('userid');
        $this->db->select('name');
        $this->db->select('surname');
        $this->db->like('concat(name, " ", surname, " ", userid)', $filter);
        $this->db->limit(10);
        $query = $this->db->get('mitu_users');
        return $query->result_array();
    }

    public function get_user_count($filter) {
        $this->db->select('count(*) size');
        $this->db->like('concat(name, " ", surname, " ", userid)', $filter);
        $query = $this->db->get('mitu_users');
        $result = $query->row();
        return $result->size;
    }

}
