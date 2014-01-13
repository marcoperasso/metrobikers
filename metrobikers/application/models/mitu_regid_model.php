<?php

class MITU_Regid_model extends MY_Model {

    var $userid;
    var $regid;

    public function __construct() {
        parent::__construct();
    }

    public function create_regid() {
        $query = $this->db->get_where('mitu_regids ', array('userid' => $this->userid, 'regid' => $this->regid));
        if ($query->num_rows() === 1) {
            return TRUE;
        }
        return $this->db->insert('mitu_regids', $this);
    }

    public function get_regids($userid) {
        $query = $this->db->select('regid')->get_where('mitu_regids ', array('userid' => $userid));
        return $query->result_array();
        
    }

}
