<?php

class Validation_key_model extends MY_Model {

    var $userid = NULL;
    var $validationkey = NULL;
    var $reason = NULL;
    var $datecreated = NULL;

    public function __construct() {
        parent::__construct();
    }

    public function create_key($userid, $reason) {
        $this->userid = $userid;
        $this->reason = $reason;
        $this->db->select('validationkey');
        $query = $this->db->get_where('validationkeys', array('userid' => $userid, 'reason' => $reason));
        if ($query->num_rows() === 1) {
            $this->assign($query->row());
            $this->datecreated = date("Y-m-d H:i:s");
            $this->db->update('validationkeys', array('datecreated' => $this->datecreated));
            return TRUE;
        }
        $this->validationkey = uniqid("", TRUE);
        $this->datecreated = date("Y-m-d H:i:s");
        return $this->db->insert('validationkeys', $this);
    }

    public function delete_key($key) {
        return $this->db->delete('validationkeys', array('validationkey' => $key));
    }

}