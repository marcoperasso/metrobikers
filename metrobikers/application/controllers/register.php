<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Register extends CI_Controller {

    public function __construct() {
        parent::__construct();
        $this->load->model('User_model');
    }

    public function index() {
        $this->load->helper('form');
        $this->load->library('form_validation');

        $this->form_validation->set_rules('firstname', 'First Name', 'required');
        $this->form_validation->set_rules('lastname', 'Last Name', 'required');
        $this->form_validation->set_rules('email', 'E Mail', 'required');

        if ($this->form_validation->run() === FALSE) {
            $this->load->view('register/register');
        } else {
            $this->User_model->set_user();
            $this->load->view('register/success');
        }
    }

}
