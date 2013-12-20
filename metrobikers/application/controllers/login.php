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
        $this->dointernallogin($mail, $pwd);
    }

    public function domobilelogin() {
        $pwd = $this->input->post('pwd');
        $mail = $this->input->post('email');
        $this->dointernallogin($mail, $pwd);
    }

    private function dointernallogin($mail, $pwd) {
        $this->load->model('User_model');

        $this->output->set_content_type('application/json');
        $this->load->library('BCrypt');
        $bcrypt = new BCrypt(15);
        
        $success = $this->User_model->get_user($mail) && $bcrypt->verify($pwd, $this->User_model->password);
        $response = array('success' => $success, 'version' => 1);
        if ($success) {
            set_user($this->User_model);
        } else {
            $response["message"] = "Login failed. Invalid user or password";
        }

        $this->output->set_output(json_encode($response));
    }

    public function dologoff() {
        $this->output->set_content_type('application/json');
        set_user(NULL);
        $response = array('success' => TRUE);
        $this->output->set_output(json_encode($response));
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */