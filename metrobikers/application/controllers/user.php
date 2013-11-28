<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class User extends MY_Controller {

    public function index() {

        $data = array();
        $user = get_user();
        if ($user != NULL) {
            $data['user'] = $user;
        }

        $this->load_view('user', "I miei dati", $data);
    }

    public function routes() {

        $data = array();
        $user = get_user();
        if ($user != NULL) {
            $data['user'] = $user;

            $this->load->model("Route_model");
            $this->Route_model->userid = $user->id;
            $routes = $this->Route_model->get_routes('0000-00-00 00:00:00');
            $data['routes'] = $routes;
        }

        $this->load_view('routes', 'I miei itinerari', $data);
    }

    public function my_ecommuters() {
        $data = array();
        $user = get_user();
        if ($user != NULL) {
            $data['user'] = $user;
            $data['linkedusers'] = $user->get_linked_users();
        }

        $this->load_view('my_ecommuters', 'I miei ECOmmuters', $data);
    }

    public function get_users_by_name_filter() {
        $user = get_user();
        if ($user == NULL) {
            $response = array('users' => array());
        } else {
            $filter = $this->input->get("filter");
            $this->load->model("User_model");
            $list = $this->User_model->get_users_by_name_filter($user->id, $filter, TRUE);
            if (count($list) == 0)
                $list = $this->User_model->get_users_by_name_filter($user->id, $filter, FALSE);
            $response = array('users' => $list);
        }
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function update() {
        $response = (object) array('result' => FALSE);
        $user = get_user();
        if ($user != NULL) {
            $this->load->model("User_model");
            if ($user->update_user($this->input->post())) {

                $user->assign($this->input->post());
                set_user($user);
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
