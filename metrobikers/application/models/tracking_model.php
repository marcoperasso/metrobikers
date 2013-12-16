<?php

class Tracking_model extends MY_Model {

    var $userid;
    var $routeid;
    var $start;
    var $end;
    var $distance;
    var $points;
    var $speedmax;
    
    public function __construct() {
        parent::__construct();
    }

  
    public function create_tracking() {
        $this->db->insert('trackings', $this);
    }

}