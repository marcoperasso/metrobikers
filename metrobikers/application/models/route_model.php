<?php

class Route_model extends MY_Model {

    var $id;
    var $userid;
    var $name;
    var $latestupdate;

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

    public function get_routes() {
        $query = $this->db->get_where('routes ', array('userid' => $this->userid));
        $result = array();
        foreach ($query->result_array() as $row) {
            $item = new Route_model();
            $item->assign($row);
            array_push($result, $item);
        }
        return $result;
    }

    public function create_route() {
        $this->db->insert('routes', $this);
        $this->db->select('id')->where('name', $this->name);
        $query = $this->db->get('routes');
        $this->id = $query->row()->id;
    }

    public function update_route() {
        $this->db->where(array('id' => $this->id, 'userid' => $this->userid));
        $this->db->update('routes', $this);
    }

}