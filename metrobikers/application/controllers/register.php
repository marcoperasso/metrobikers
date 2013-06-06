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
        $this->load->helper('form');
        $this->load->library('form_validation');
        $this->form_validation->set_rules('password', 'Password', 'required');
        $key = $this->input->post('userkey');
        $data['key'] = $key;
        if ($this->form_validation->run() === TRUE) {
            if ($this->User_model->get_user_by_key($key)) {
                $data['user'] = $this->User_model;
                $this->db->trans_begin();
                $this->User_model->activate_user();
                $this->Validation_key_model->delete_key($this->User_model->id);
                $this->db->trans_commit();
                $this->load->view("register/useractivated", $data);
            } else {
                $this->load->view("register/invalidkey", $data);
            }
        }
    }

    public function preactivate() {
        $key = $this->input->get("userkey");
        $data['key'] = $key;
        if ($this->User_model->get_user_by_key($key)) {
            $this->load->helper('form');
            $this->load->library('form_validation');
            $this->load->view("register/activate", $data);
        } else {
            $this->load->view("register/invalidkey", $data);
        }
    }

    public function index() {

        $this->load->helper('form');
        $this->load->library('form_validation');

        $this->form_validation->set_rules('name', 'First Name', 'required');
        $this->form_validation->set_rules('surname', 'Last Name', 'required');
        $this->form_validation->set_rules('mail', 'E Mail', 'required');
        $this->form_validation->set_rules('birthdate', 'Birth date', 'required');
        $this->form_validation->set_rules('captcha', 'Verification code', 'required');
        if ($this->form_validation->run() === FALSE) {
            $this->load->view('register/register');
        } else {

            $this->load->library('session');
            $captcha = $this->session->userdata('captcha');
            $word = $this->input->post("captcha");
            if ($captcha != $word)
                throw new Exception("Invalid verification code!");
            $this->db->trans_begin();
            $this->User_model->create_user();
            $this->Validation_key_model->create_key($this->User_model->id);
            $this->db->trans_commit();
            $url = my_base_url() . "Register/preactivate?userkey=" . urlencode($this->Validation_key_model->validationkey);
            $this->send_mail($this->User_model->mail, "Registration submitted", "Follow this link: <a href=\"" . $url . "\">" . $url . "</a> to activate your registration.");
            $data["user"] = $this->User_model;
            $this->load->view('register/userregistered', $data);
        }
    }

}