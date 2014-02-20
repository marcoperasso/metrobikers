<?php

class HIA_User_position_model extends MY_Model {

    var $phone;
    var $lat;
    var $lon;
    var $time;
    var $gps;
    public function __construct() {
        parent::__construct();
    }

    public function exist_position() {
        $this->db->from('hia_userpositions');
        $this->db->where('phone', $this->phone);
        $ret = $this->db->count_all_results();
        return $ret !== 0;
    }

    public function purge_positions() {
        $this->db->from('hia_userpositions');
        $t = time() - 900; //15 minuti fa
        $this->db->where('time <', date('Y-m-d H:i:s', $t));
        $this->db->delete();
    }

    public function get_positions($phone) {
        $select = "SELECT lat, lon, time, gps, hia_users.phone as userphone"  .
                " FROM hia_userpositions " .
                " JOIN hia_users ON hia_users.phone = hia_userpositions.phone " .
                " JOIN hia_connections ON hia_users.phone = hia_connections.phonefrom ".
                " where phoneto = " . $this->db->escape($phone) ;
        $query = $this->db->query($select);
        return $query->result_array(); 
    }

    public function save_position() {
        if ($this->exist_position()) {
            $this->update_position();
        } else {
            $this->create_position();
        }
    }

    public function update_position() {
        $this->db->where(array('phone' => $this->phone));
        $this->db->update('hia_userpositions', $this);
    }

    public function create_position() {
        $this->db->insert('hia_userpositions', $this);
    }

}
