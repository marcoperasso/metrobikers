<?php

class Tracking_points_model extends MY_Model {

    var $trackingid;
    var $time;
    var $lat;
    var $lon;

    public function __construct() {
        parent::__construct();
    }

    public function create_point() {
        $this->db->insert('trackingpoints', $this);
    }

}