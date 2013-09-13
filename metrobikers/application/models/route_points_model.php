<?php

class Route_points_model extends MY_Model {

    var $id;
    var $routeid;
    var $lat;
    var $lon;
    var $ele;
    var $time;

    public function __construct() {
        parent::__construct();
    }

    public function get_point() {
        $query = $this->db->get_where('routepoints ', array('routeid' => $this->routeid, 'id' => $this->id));
        if ($query->num_rows() === 1) {
            $this->assign($query->row());
            return TRUE;
        }
        return FALSE;
    }

    public function get_points() {
        $query = $this->db->get_where('routepoints ', array('routeid' => $this->routeid));
        $result = array();
        foreach ($query->result_array() as $row) {
            $item = new Route_points_model();
            $item->assign($row);
            array_push($result, $item);
        }
        return $result;
    }

    public function delete_points() {
        $this->db->where(array('routeid' => $this->routeid));
        $this->db->delete('routepoints');
    }
    public function update_point() {
        $this->db->where(array('routeid' => $this->routeid, 'id' => $this->id));
        $this->db->update('routepoints', $this);
    }

    public function create_point() {
        $this->db->insert('routepoints', $this);
    }

}