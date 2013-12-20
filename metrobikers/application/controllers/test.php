<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Test extends MY_Controller {

    public function __construct() {
        parent::__construct();
    }

    public function index() {
        $this->load->library('BCrypt');
        $bcrypt = new BCrypt(15);
        $hash = $bcrypt->hash("a");

        /*$query = $this->db->get('users');
        $result = $query->result_array();
        foreach ($result as &$value) {
            $value = (object) $value;
            $i = strlen($value->password);
            if ($i < 60) {
                $value->password = $bcrypt->hash($value->password);
                $i = strlen($value->password);
                $this->db->where('id', $value->id);
                $this->db->update('users', array('password' => $value->password));
            }
        }*/
        echo strlen($hash);
    }

    public function registration_mail_content() {
        $data = array("user_draft" => (object) array("name" => "UserName"), "validationkey" => "AAAAAA");
        $this->load->view('register/registermailcontent', $data);
    }

    public function resetpwd_mail_content() {
        $data = array("user_draft" => (object) array("name" => "UserName"), "validationkey" => "AAAAAA");
        $this->load->view('register/resetpwdmailcontent', $data);
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */
