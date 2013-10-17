<?php

class Tracking_model extends MY_Model {

    var $id;
    var $userid;
    var $routeid;
    var $time;

    public function __construct() {
        parent::__construct();
    }

  
    public function create_tracking() {
        $this->db->insert('trackings', $this);
        $this->id = $this->db->insert_id();
    }

}