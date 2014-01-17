<?php

class MITU_User_position_model extends MY_Model {

    var $userid;
    var $lat;
    var $lon;
    var $time;
    var $gps;
    public function __construct() {
        parent::__construct();
    }

    public function exist_position() {
        $this->db->from('mitu_userpositions');
        $this->db->where('userid', $this->userid);
        $ret = $this->db->count_all_results();
        return $ret !== 0;
    }

    public function purge_positions() {
        $this->db->from('mitu_userpositions');
        $t = time() - 900; //15 minuti fa
        $this->db->where('time <', date('Y-m-d H:i:s', $t));
        $this->db->delete();
    }

    public function get_positions($userid) {
        $select = "SELECT lat, lon, name, surname, time, gps, mitu_users.userid " .
                "FROM mitu_userpositions " .
                " JOIN mitu_users ON mitu_users.id = mitu_userpositions.userid " .
                " right JOIN mitu_connections ON mitu_users.id = mitu_connections.idfrom ".
                " where idto = " . $this->db->escape($userid);
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
        $this->db->where(array('userid' => $this->userid));
        $this->db->update('mitu_userpositions', $this);
    }

    public function create_position() {
        $this->db->insert('mitu_userpositions', $this);
    }

}
