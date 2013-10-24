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
        $this->db->select('verb, content');
        $query = $this->db->get_where('verbs', array('verb' => $name));
        return $query->num_rows() == 0 ? NULL : $query->row();
    }
	public function get_hints($constraint)
	{
		$this->db->select('verb');
		$this->db->from('verbs');
		$this->db->like('verb', $constraint, 'after');
        $query = $this->db->get();
        return $query->result_array();
	}
     public function getall() {
        $this->db->select('verb');
        $query = $this->db->get('verbs');
        return $query->result_array();
    }
    
}