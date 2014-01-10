<?php

class MITU_Regid_model extends MY_Model {

    var $userid;
    var $regid;

    public function __construct() {
        parent::__construct();
    }

    public function create_regid() {
        $this->db->insert('mitu_regids', $this);
    }

    public function get_regids($userid) {
        $query = $this->db->get_where('mitu_regids ', array('userid' => $userid));
        return $query->result_array();
    }

}
