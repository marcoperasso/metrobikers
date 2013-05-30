<?php

class User_model extends CI_Model {

    public function __construct() {
        $this->load->database();
    }

    public function get_user($email = NULL) {
        if ($email === NULL) {
            $query = $this->db->get('user');
            return $query->result_array();
        }

        $query = $this->db->get_where('email', array('email' => $email));
        return $query->row_array();
    }

    public function set_user() {
        $data = array(
            'email' => $this->input->post('email'),
            'firstname' => $this->input->post('firstname'),
            'lastname' => $this->input->post('lastname'),
        );

        return $this->db->insert('user', $data);
    }

}