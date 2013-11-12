<?php

class User_position_model extends MY_Model {

    var $userid;
    var $lat;
    var $lon;
    var $time;

    public function __construct() {
        parent::__construct();
    }

    public function exist_position() {
        $this->db->from('userpositions');
        $this->db->where('userid', $this->userid);
        $ret = $this->db->count_all_results();
        return $ret !== 0;
    }

    public function purge_positions() {
        $this->db->from('userpositions');
        $t = time() - 900; //15 minuti fa
        $this->db->where('time <', date('Y-m-d H:i:s', $t));
        $this->db->delete();
    }
    
    public function get_positions($left, $top, $right, $bottom) 
    {
        $this->commonQuery();
        $query = $this->db->get_where('userpositions', array('lat >' => $left, 'lat <' => $right, 'lon >' => $top, 'lon <' => $bottom));
        return $query->result_array();
    }
    public function get_positions_by_name($name) 
    {
        $this->commonQuery();
        $this->db->like('name', $name);
        $query = $this->db->get('userpositions');
        return $query->result_array();
    }
    private function commonQuery()
    {
        $this->db->select("lat");
        $this->db->select("lon");
        $this->db->select("name");
        $this->db->select("surname");
        $this->db->select("time");
        $this->db->select("mail");
        $this->db->select("users.id as userid");
        $this->db->join('users', 'users.id = userpositions.userid');
    }
    
    public function save_position() {
        if ($this->exist_position())
            $this->update_position();
        else
            $this->create_position();
    }

    public function update_position() {
        $this->db->where(array('userid' => $this->userid));
        $this->db->update('userpositions', $this);
    }

    public function create_position() {
        $this->db->insert('userpositions', $this);
    }

}