<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Activateuser extends MY_Controller {

    public function __construct() {
        parent::__construct();
        $this->load->model('User_model');
        $this->load->model('Validation_key_model');
    }

    public function index() {
        $key = $_REQUEST["key"];
        if ($this->User_model->get_user_by_key($key)) {
            $data['user'] = $this->User_model;
            $this->load->helper('form');
            $this->load->library('form_validation');
            $this->load->view("activateuser/activate", $data);
        } else {
            $data['key'] = $key;
            $this->load->view("activateuser/invalidkey", $data);
        }
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */