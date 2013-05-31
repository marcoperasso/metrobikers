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

        $this->form_validation->set_rules('name', 'First Name', 'required');
        $this->form_validation->set_rules('surname', 'Last Name', 'required');
        $this->form_validation->set_rules('mail', 'E Mail', 'required');

        if ($this->form_validation->run() === FALSE) {
            $this->load->view('register/register');
        } else {
            $a = $this->User_model->set_user();

            $this->load->library('email');
            $config['protocol'] = 'smtp';
            $config['charset'] = 'utf-8';
            $config['wordwrap'] = TRUE;
            $config['smtp_host'] = "smtp.gmail.com";
            $config['smtp_user'] = "mtbgroupscout";
            $config['smtp_pass'] = "montoggio";
            $this->email->initialize($config);
            $this->email->from('mtbgroupscout@gmail.com', 'Marco Perasso');
            
            $this->email->to("marco.perasso@gmail.com");//$a['mail']);

            $this->email->subject('Registraton submitted');
            $this->email->message('Click this link to activate your registration');

            $this->email->send();
            $this->load->view('register/success');
        }
    }

}
