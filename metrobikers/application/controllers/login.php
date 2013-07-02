<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Login extends MY_Controller {

    public function index() {
        $this->load->view('login');
    }

    public function dologin() {
        $this->load->library('Crypter');
        $pwd = $this->crypter->decrypt($this->input->get('pwd'));
        $mail = $this->input->get('email');
        $this->load->model('User_model');

        $this->output->set_content_type('application/json');
        $success = $this->User_model->get_user($mail) && $this->User_model->password == $pwd;
        $response = array('success' => $success);
        if ($success) {
            $this->load->library('session');
            $this->session->set_userdata("user", $this->User_model);
        } else {
            $response["message"] = "Login failed. Invalid user or password";
        }

        $this->output->set_output(json_encode($response));
    }

    public function dologoff() {
        $this->output->set_content_type('application/json');
        $this->load->library('session');
        $this->session->set_userdata("user", NULL);
        $response = array('success' => TRUE);
        $this->output->set_output(json_encode($response));
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */