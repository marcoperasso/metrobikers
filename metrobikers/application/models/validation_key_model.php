<?php

class Validation_key_model extends MY_Model {

    var $userid = NULL;
    var $validationkey = NULL;

    public function __construct() {
        parent::__construct();
    }

    public function get_key($user) {
        $query = $this->db->get_where('validationkeys', array('userid' => user));
        $this->assign($query->row_array());
    }

    public function create_key($user) {
        $this->userid = $user;
        $this->validationkey = uniqid("", TRUE);

        return $this->db->insert('validationkeys', $this);
    }

}