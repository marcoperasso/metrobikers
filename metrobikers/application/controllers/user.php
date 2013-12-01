<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class User extends MY_Controller {

    public function index() {
        if (!$this->validate_login())
            return;
        $data = array();
        $data['user'] = $this->user;

        $this->load_view('user', "I miei dati", $data);
    }

    public function routes() {
        if (!$this->validate_login())
            return;
        $this->load->model("Route_model");
        $this->Route_model->userid = $this->user->id;
        $routes = $this->Route_model->get_routes('0000-00-00 00:00:00');
        $data = array();
        $data['user'] = $this->user;
        $data['routes'] = $routes;

        $this->load_view('routes', 'I miei itinerari', $data);
    }

    public function preconnect($userid) {
        if (!$this->validate_login())
            return;
        if ($this->User_model->get_user_by_id($userid)) {
            $this->load->model('Validation_key_model');
            $this->Validation_key_model->create_key($this->user->id);
            $data["user_contacted"] = $this->User_model;
            $data["user"] = $this->user;
            $data["validationkey"] = $this->Validation_key_model->validationkey;
            $view = $this->load->view('mail/contactusermailcontent', $data, TRUE);
            $this->send_mail($this->User_model->mail, lang("contact_submitted"), $view);
            $this->load_view('connections/usercontacted', "Richiesta di contatto inoltrata", $data);
        }
    }

    public function connect() {
        if (!$this->validate_login())
            return;
        $key = $this->input->get("userkey");
        $data['key'] = $key;
        if ($this->User_model->get_user_by_key($key)) {
            if ($this->User_model->id == $this->user->id) {
                $this->load_view("connections/error", "Chiave di collegamento non valida", $data);
                return;
            }
            $this->load->model('Validation_key_model');
            $this->db->trans_begin();
            $this->user->insert_linked_user($this->User_model->id);
            $this->Validation_key_model->delete_key($key);
            $this->db->trans_commit();
            $this->my_ecommuters();
        } else {
            $this->load_view("connections/error", "Chiave di collegamento non valida", $data);
        }
    }

    public function my_ecommuters() {
        if (!$this->validate_login())
            return;

        $data = array();
        $data['user'] = $this->user;
        $data['linkedusers'] = $this->user->get_linked_users();
        $this->load_view('connections/my_ecommuters', 'Il mio gruppo', $data);
    }

    public function get_not_linked_users() {
        if ($this->user == NULL) {
            $response = array('users' => array());
        } else {
            $filter = $this->input->get("filter");
            $exact = $this->input->get("exact");
            $list = $this->user->get_not_linked_users($this->user->id, $filter, TRUE, $exact);
            if (!$exact && count($list) == 0)
                $list = $this->user->get_not_linked_users($this->user->id, $filter, FALSE, FALSE);
            $response = array('users' => $list);
        }
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function update() {
        $response = (object) array('result' => FALSE);
        if ($this->user != NULL) {
            if ($this->user->update_user($this->input->post())) {

                $this->user->assign($this->input->post());
                set_user($this->user);
                $response->result = TRUE;
            }
        }

        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */
