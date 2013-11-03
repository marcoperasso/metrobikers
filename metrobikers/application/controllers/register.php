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
                set_user($this->User_model);
                $this->load_view("register/useractivated", "Registrazione attivata", $data);
            } else {
                $this->load_view("register/invalidkey", "Chiave di attivazione non valida", $data);
            }
        }
    }

    public function preactivate() {
        $key = $this->input->get("userkey");
        $data['key'] = $key;
        if ($this->User_model->get_user_by_key($key)) {
            $this->load->helper('form');
            $this->load->library('form_validation');
            $data["user_draft"] = $this->User_model;
            $this->load_view("register/activate", "Attiva la registrazione", $data);
        } else {
            $this->load_view("register/invalidkey", "Chiave di attivazione non valida", $data);
        }
    }

    public function reset_pwd() {
        $mail = $this->input->post('email');
        if ($mail && $this->User_model->get_user($mail)) {
            $this->db->trans_begin();
            $this->Validation_key_model->create_key($this->User_model->id);
            $this->db->trans_commit();
            $data["user_draft"] = $this->User_model;
            $data["validationkey"] = $this->Validation_key_model->validationkey;
            $view = $this->load->view('register/resetpwdmailcontent', $data, TRUE);
            $this->send_mail($this->User_model->mail, lang("reset_pwd_submitted"), $view);
            $this->load_view('register/resettedpwd', "Ripristino password", $data);
        } else {
            $data["mail"] = $mail;
            $this->load_view('register/invalidmail', "Email non valida", $data);
        }
    }

    public function index() {

        $this->load->helper('form');
        $this->load->library('form_validation');

        $this->form_validation->set_rules('name', 'First Name', 'required');
        $this->form_validation->set_rules('surname', 'Last Name', 'required');
        $this->form_validation->set_rules('mail', 'E Mail', 'required');
        if ($this->form_validation->run() === FALSE) {
            $this->load_view('register/register', "Registrazione utente");
        } else {
            $this->db->trans_begin();
            $this->User_model->create_user();
            $this->Validation_key_model->create_key($this->User_model->id);
            $this->db->trans_commit();
            $data["user_draft"] = $this->User_model;
            $data["validationkey"] = $this->Validation_key_model->validationkey;
            $view = $this->load->view('register/registermailcontent', $data, TRUE);
            $this->send_mail($this->User_model->mail, lang("registration_submitted"), $view);
            $this->load_view('register/userregistered', "Utente registrato", $data);
        }
    }

}
