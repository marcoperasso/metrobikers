<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Mobile extends MY_Controller {

    public function version() {

        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode(array('version' => 1)));
    }

    public function user() {
        $user = get_user();
        $response = NULL;
        if ($user != NULL) {
            $response = array(
                'logged' => TRUE,
                'name' => $user->name,
                'surname' => $user->surname,
                'birthdate' => $user->birthdate,
            );
        } else {
            $response = array(
                'logged' => FALSE
            );
        }
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function save_route() {
        $user = get_user();
        $response = NULL;
        if ($user != NULL) {
            $json = $this->input->post("route");
            $route = json_decode($json);
            $this->load->model("Route_model");
            $this->load->model("Route_points_model");
            try {
                if (!$this->Route_model->get_route($user->id, $route->name))
                    $this->Route_model->create_route($user->id, $route->name);
                $response = array(
                    'saved' => TRUE
                );
            } catch (Exception $exc) {
                $response = array(
                    'saved' => FALSE,
                    'message' => $exc->getTraceAsString());
            }
        } else {
            $response = array(
                'saved' => FALSE
            );
        }
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */