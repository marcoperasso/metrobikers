<?php

class User_on_route_model extends MY_Model {

    var $userid;
    var $routeid;

    public function __construct() {
        parent::__construct();
    }

    public function get_routes($userid) {
        $this->db->select('routeid');
        $query = $this->db->get_where('usersonroutes ', array('userid' => $userid));
        $ret = array();
		$x = 0;
		foreach($query->result_array() as $route)
		{
			$ret[$x] = $route["routeid"];
			$x++;
		}
		return $ret;
    }

    public function purge($userid) {
        $this->db->from('usersonroutes');
        $query = $this->db->where('userid', $userid);
        return $query->delete();
    }

    public function save() {
        $this->db->insert('usersonroutes', $this);
    }

}