<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Test extends MY_Controller {

    public function __construct() {
        parent::__construct();
    }

    public function index() {
        phpinfo();
        //$this->load->model('User_model');
        // $this->User_model->get_user("marco.perasso@microarea.it");
        // set_user($this->User_model);
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
