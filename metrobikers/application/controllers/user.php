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

    public function connect() {
        if (!$this->validate_login())
            return;
    }

    public function my_ecommuters() {
        if (!$this->validate_login())
            return;

        $data = array();
        $data['user'] = $this->user;
        $data['linkedusers'] = $this->user->get_linked_users();
        $this->load_view('my_ecommuters', 'Il mio gruppo', $data);
    }

    public function get_users_by_name_filter() {
        if ($this->user == NULL) {
            $response = array('users' => array());
        } else {
            $filter = $this->input->get("filter");
            $list = $this->User_model->get_users_by_name_filter($this->user->id, $filter, TRUE);
            if (count($list) == 0)
                $list = $this->User_model->get_users_by_name_filter($this->user->id, $filter, FALSE);
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
