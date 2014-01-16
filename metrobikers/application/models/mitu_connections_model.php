<?php

class MITU_Connections_model extends MY_Model {

    var $idfrom;
    var $idto;

    public function __construct() {
        parent::__construct();
    }

    public function purge($userid) {
        $this->db->from('mitu_connections');
        $query = $this->db->where('idfrom', $userid);
        return $query->delete();
    }
    public function create_connection() {
        $query = $this->db->get_where('mitu_connections ', array('idfrom' => $this->idfrom, 'idto' => $this->idto));
        if ($query->num_rows() === 1) {
            return TRUE;
        }
        return $this->db->insert('mitu_connections', $this);
    }

}
