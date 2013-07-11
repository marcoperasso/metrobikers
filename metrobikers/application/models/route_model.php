<?php

class Route_model extends MY_Model {

    var $id;
    var $userid;
    var $name;

    public function __construct() {
        parent::__construct();
    }

    public function get_route() {
        $query = $this->db->get_where('routes ', array('userid' => $this->userid, 'name' => $this->name));
        if ($query->num_rows() >= 1) {
            $this->assign($query->row());
            return TRUE;
        }
        return FALSE;
    }

    public function create_route() {
        $this->db->insert('routes', $this);
        $this->db->select('id')->where('name', $this->name);
        $query = $this->db->get('routes');
        $this->id = $query->row()->id;
    }

   

}