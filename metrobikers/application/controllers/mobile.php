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
                $this->Route_model->name = $route->name;
                $this->Route_model->userid = $user->id;
                $this->db->trans_begin();
                if (!$this->Route_model->get_route())
                    $this->Route_model->create_route();
                foreach ($route->points as $point) {
                    $this->Route_points_model->id = $point->id;
                    $this->Route_points_model->routeid = $this->Route_model->id;
                    if (!$this->Route_points_model->get_point()) {
                        $this->Route_points_model->lat = $point->lat;
                        $this->Route_points_model->lon = $point->lon;
                        $this->Route_points_model->ele = $point->ele;
                        $this->Route_points_model->time = date('Y-m-d H:i:s', $point->time);
                        $this->Route_points_model->create_point();
                    }
                }
                $this->db->trans_commit();
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