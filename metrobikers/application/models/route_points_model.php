<?php

class Route_points_model extends MY_Model {

    var $userid;
    var $lat;
    var $lon;
    var $ele;
    var $time;
    
    public function __construct() {
        parent::__construct();
    }
}