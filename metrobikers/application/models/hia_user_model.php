<?php

class HIA_User_model extends MY_Model {

    var $phone;
    var $mail;
    var $password;
    var $regid;

    public function __construct() {
        parent::__construct();
    }

    public function create_user() {
        $this->db->insert('hia_users', $this);
    }

    public function update_user() {
        $this->db->where('phone', $this->phone);
        return $this->db->update('hia_users', array(
                    'mail' => $this->mail,
                    'regid' => $this->regid,
                    'password' => $this->password
        ));
    }

    public function get_user($phone) {
        $query = $this->db->get_where('hia_users ', array('phone' => $phone));
        if (!$query)
            return FALSE;
        if ($query->num_rows() === 1) {
            $this->assign($query->row());
            return TRUE;
        }
        return FALSE;
    }

}
