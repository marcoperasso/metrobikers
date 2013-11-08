<?php

class Route_model extends MY_Model {

    var $id;
    var $userid;
    var $name;
    var $latestupdate;
    private $_points = NULL;

    public function __construct() {
        parent::__construct();
    }

    public function get_route() {
        $query = $this->db->get_where('routes ', array('userid' => $this->userid, 'name' => $this->name));
        if ($query->num_rows() >= 1) {
            $this->assign($query->row());
            $this->latestupdate = strtotime($this->latestupdate);
            $this->_points = NULL;
            return TRUE;
        }
        return FALSE;
    }

    public function get_route_by_id() {
        $query = $this->db->get_where('routes ', array('userid' => $this->userid, 'id' => $this->id));
        if ($query->num_rows() >= 1) {
            $this->assign($query->row());
            $this->latestupdate = strtotime($this->latestupdate);

            $this->_points = NULL;
            return TRUE;
        }
        return FALSE;
    }

    public function get_routes($latestupdate) {
        $query = $this->db->get_where('routes ', array('userid' => $this->userid, 'latestupdate >' => $latestupdate));
        $result = array();
        foreach ($query->result_array() as $row) {
            $item = new Route_model();
            $item->assign($row);
            $item->latestupdate = strtotime($item->latestupdate);

            array_push($result, $item);
        }
        return $result;
    }

    public function create_route() {
        $this->db->insert('routes', $this);
        $this->_points = NULL;
        $this->id = $this->db->insert_id();

        $this->insert_points(FALSE);
    }

    public function update_route() {
        $this->db->where(array('id' => $this->id, 'userid' => $this->userid));
        $this->db->update('routes', $this);
        $this->insert_points(TRUE);
    }

    private function insert_points($clear) {
        $this->load->model("Route_points_model");
        if ($clear)
            $this->Route_points_model->delete_points($this->id);

        $this->Route_points_model->create_points($this->id, $this->get_points());
        
    }

    private function load_points() {
        $this->load->model("Route_points_model");
        $this->_points = $this->Route_points_model->get_points($this->id);
        foreach ($this->_points as &$point) {
            $point->time = strtotime($point->time);
        }
    }

    public function get_points() {
        if (!isset($this->_points)) {
            $this->load_points();
        }
        return $this->_points;
    }

    public function set_points($points) {
        $this->_points = $points;
    }

}