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

    public function update() {
        $response = (object)array('result' => FALSE);
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
