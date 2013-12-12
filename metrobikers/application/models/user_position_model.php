<?php

class User_position_model extends MY_Model {
    var $userid;
    var $lat;
    var $lon;
    var $time;

    public function __construct() {
        parent::__construct();
    }

    public function exist_position() {
        $this->db->from('userpositions');
        $this->db->where('userid', $this->userid);
        $ret = $this->db->count_all_results();
        return $ret !== 0;
    }

    public function purge_positions() {
        $this->db->from('userpositions');
        $t = time() - 900; //15 minuti fa
        $this->db->where('time <', date('Y-m-d H:i:s', $t));
        $this->db->delete();
    }

    public function get_positions($left, $top, $right, $bottom, $userid, $search_userid) {
        $select = "SELECT distinct lat, lon, ".
                    "if (showname = 0 or (showname =  1 and (userid1 = " . $userid . " or userid2 = " . $userid . ")), name, 'ECOmmuter') as name, " .
                    "if (showname = 0 or (showname =  1 and (userid1 = " . $userid . " or userid2 = " . $userid . ")), surname, 'Anomimo') as surname, " .
                    "time, users.id as userid " .
                    "FROM (userpositions) " .
                    "JOIN users ON users.id = userpositions.userid ".
                    "LEFT JOIN linkedusers ON users.id = linkedusers.userid1 OR users.id = linkedusers.userid2 " .
                    "WHERE (showposition =  0 OR (showposition =  1 and (userid1 = " . $userid . " or userid2 = " . $userid . "))) ".
                    "AND (lat BETWEEN  '" . $left . "' AND '" . $right . "' AND lon BETWEEN '" . $top . "' AND '" . $bottom . "' OR users.id = " . $search_userid . ")";
        $query = $this->db->query($select);
        return $query->result_array();
    }

    public function get_positions_by_name($name) {
        $this->db->select("lat");
        $this->db->select("lon");
        $this->db->select("name");
        $this->db->select("surname");
        $this->db->select("time");
        $this->db->select("users.id as userid");
        $this->db->join('users', 'users.id = userpositions.userid');
        $this->db->like('concat(name, " ", surname)', $name);
        $this->db->limit(10);
        $query = $this->db->get('userpositions');
        return $query->result_array();
    }

    public function get_positions_count_by_name($name) {
        $this->db->select('count(*) size');
        $this->db->from('userpositions');
        $this->db->join('users', 'users.id = userpositions.userid');
        $this->db->like('concat(name, " ", surname)', $name);
        $query = $this->db->get();
        $result = $query->row();
        return $result->size;
    }
    public function save_position() {
        if ($this->exist_position())
            $this->update_position();
        else
            $this->create_position();
    }

    public function update_position() {
        $this->db->where(array('userid' => $this->userid));
        $this->db->update('userpositions', $this);
    }

    public function create_position() {
        $this->db->insert('userpositions', $this);
    }

}