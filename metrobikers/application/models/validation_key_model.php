<?php

class Validation_key_model extends MY_Model {

    var $userid = NULL;
    var $validationkey = NULL;

    public function __construct() {
        parent::__construct();
    }

    public function create_key($user) {
        $this->userid = $user;
        $this->validationkey = uniqid("", TRUE);

        return $this->db->insert('validationkeys', $this);
    }

    public function delete_key($key) {
        return $this->db->delete('validationkeys', array('validationkey' => $key)); 
    }

}