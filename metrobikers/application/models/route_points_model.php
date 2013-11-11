<?php

class Route_points_model extends MY_Model {

    var $lat;
    var $lon;
    var $time;

    public function __construct() {
        parent::__construct();
    }

    public function get_points($routeid) {
        $this->db->order_by('time', 'asc');
        $query = $this->db->get_where('routepoints ', array('routeid' => $routeid));
        $result = array();
        foreach ($query->result_array() as $row) {
            $item = new Route_points_model();
            $item->assign($row);
            array_push($result, $item);
        }
        return $result;
    }

    public function delete_points($routeid) {
        $this->db->where(array('routeid' => $routeid));
        $this->db->delete('routepoints');
    }

    public function create_points($routeid, $points) {
        foreach ($points as $point) {
            $pt = array(
            'routeid' => $routeid,
            'lat' => $point->lat,
            'lon' => $point->lon,
            'time' => date('Y-m-d H:i:s', $point->time)
            ); 
            $this->db->insert('routepoints', $pt);
        }
    }

}