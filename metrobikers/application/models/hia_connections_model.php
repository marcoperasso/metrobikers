<?php

class HIA_Connections_model extends MY_Model {

    var $phonefrom;
    var $phoneto;

    public function __construct() {
        parent::__construct();
    }

    public function purge($phone) {
        $this->db->from('hia_connections');
        $query = $this->db->where('phonefrom', $phone);
        return $query->delete();
    }
    public function create_connection() {
        $query = $this->db->get_where('hia_connections ', array('phonefrom' => $this->phonefrom, 'phoneto' => $this->phoneto));
        if ($query->num_rows() === 1) {
            return TRUE;
        }
        return $this->db->insert('hia_connections', $this);
    }

}
