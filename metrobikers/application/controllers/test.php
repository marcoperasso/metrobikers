<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Test extends MY_Controller {

    public function __construct() {
        parent::__construct();
    }

    public function index() {
       echo sprintf("%1s ha percorso %2.2f Km percorrendo l'itinerario %3s", "pp", (3456 / 1000), "Name");
    }

    public function registration_mail_content() { 
        $data = array("user_draft" => (object)array("name" =>"UserName"), "validationkey" => "AAAAAA");
        $this->load->view('register/registermailcontent', $data);
    }

    public function resetpwd_mail_content() { 
        $data = array("user_draft" => (object)array("name" =>"UserName"), "validationkey" => "AAAAAA");
        $this->load->view('register/resetpwdmailcontent', $data);
    }
}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */
