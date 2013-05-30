<?php

class User_model extends CI_Model {

    public function __construct() {
        $this->load->database();
    }

    public function get_user($mail = NULL) {
        if ($mail === NULL) {
            $query = $this->db->get('users');
            return $query->result_array();
        }

        $query = $this->db->get_where('mail', array('mail' => $mail));
        return $query->row_array();
    }

    public function set_user() {
        $data = array(
            'mail' => $this->input->post('mail'),
            'name' => $this->input->post('name'),
            'surname' => $this->input->post('surname'),
            'birthdate' => date('Y-m-d', strtotime($this->input->post('birthdate'))),
            'surname' => $this->input->post('surname'),
            'active' => FALSE,
            'gender' => $this->input->post('gender'),
        );

        return $this->db->insert('users', $data);
    }

}