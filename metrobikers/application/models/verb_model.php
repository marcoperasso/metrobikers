<?php

class Verb_model extends MY_Model {

    var $verb;
    var $content;

    public function __construct() {
        parent::__construct();
    }

    public function insert() {
        $this->db->insert('verbs', $this);
    }
    public function get($name) {
        $this->db->select('content');
        $query = $this->db->get_where('verbs', array('verb' => $name));
        return $query->num_rows() == 0 ? "" : $query->row()->content;
    }
     public function getall() {
        $this->db->select('verb');
        $query = $this->db->get('verbs');
        return $query->result_array();
    }
    
}