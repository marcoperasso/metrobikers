<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Register extends MY_Controller {

    public function __construct() {
        parent::__construct();
        $this->load->model('User_model');
        $this->load->model('Validation_key_model');
    }

    public function activate() {
        
    }

    public function index() {

        $this->load->helper('form');
        $this->load->library('form_validation');

        $this->form_validation->set_rules('name', 'First Name', 'required');
        $this->form_validation->set_rules('surname', 'Last Name', 'required');
        $this->form_validation->set_rules('mail', 'E Mail', 'required');
        $this->form_validation->set_rules('birthdate', 'Birth date', 'required');

        if ($this->form_validation->run() === FALSE) {
            $this->load->view('register/register');
        } else {
            $this->db->trans_begin();
            $this->User_model->set_user();
            $this->Validation_key_model->create_key($this->User_model->id);
            $this->db->trans_commit();
            $url = my_base_url() . "Activateuser?key=" . urlencode($this->Validation_key_model->validationkey);
            $this->send_mail($this->User_model->mail, "Registration submitted", "Follow this link: <a href=\"" . $url . "\">" . $url . "</a> to activate your registration.");

            $this->load->view('register/success');
        }
    }

}
