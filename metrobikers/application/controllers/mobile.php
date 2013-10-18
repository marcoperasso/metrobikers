<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Mobile extends MY_Controller {

    public function version() {

        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode(array('version' => 1)));
    }

    public function user_logged() {
        $user = get_user();
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode(array('logged' => $user !== NULL)));
    }

    public function user() {
        $user = get_user();
        $response = NULL;
        if ($user != NULL) {
            $response = array(
                'logged' => TRUE,
                'userid' => $user->id,
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

    public function get_routes($latestupdate) {
        $user = get_user();
        if ($user != NULL) {
            $this->load->model("Route_model");
            $this->load->model("Route_points_model");
            $this->Route_model->userid = $user->id;
            $response = $this->Route_model->get_routes(date('Y-m-d H:i:s', $latestupdate));
            foreach ($response as $route) {
                $route->latestupdate = strtotime($route->latestupdate);
                $this->Route_points_model->routeid = $route->id;
                $route->points = $this->Route_points_model->get_points();
                foreach ($route->points as $point) {
                    $point->time = strtotime($point->time);
                }
            }
        } else {
            $response = array();
        }
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function update_position() {

        $json = $this->input->post("data");
        $point = json_decode($json);
        $this->load->model("User_position_model");

        $this->User_position_model->userid = $point->userid;
        $this->User_position_model->lat = $point->lat;
        $this->User_position_model->lon = $point->lon;
        $this->User_position_model->time = date('Y-m-d H:i:s', $point->time);
        $this->User_position_model->save_position();

        $response = array('saved' => TRUE);

        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function get_positions($left, $top, $right, $bottom) {
        $this->load->model("User_position_model");
        $this->User_position_model->purge_positions();
        $response = $this->User_position_model->get_positions($left, $top, $right, $bottom);

        foreach ($response as &$point) {
            $point["time"] = strtotime($point["time"]);
        }
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function save_route() {
        $user = get_user();
        $response = NULL;
        if ($user != NULL) {
            $json = $this->input->post("data");
            $route = json_decode($json);
            $this->load->model("Route_model");
            $this->load->model("Route_points_model");
            try {
                $this->Route_model->name = $route->name;

                $this->Route_model->userid = $user->id;
                $this->db->trans_begin();
                if (!$this->Route_model->get_route()) {
                    $this->Route_model->latestupdate = date('Y-m-d H:i:s', $route->latestupdate);
                    $this->Route_model->create_route();
                } else {
                    $this->Route_model->latestupdate = max(array(date('Y-m-d H:i:s', $route->latestupdate), $this->Route_model->latestupdate));
                    $this->Route_model->update_route();
                }

                $this->Route_points_model->delete_points();

                foreach ($route->points as $point) {
                    $this->Route_points_model->id = $point->id;
                    $this->Route_points_model->routeid = $this->Route_model->id;
                    $this->Route_points_model->lat = $point->lat;
                    $this->Route_points_model->lon = $point->lon;
                    $this->Route_points_model->time = date('Y-m-d H:i:s', $point->time);
                    $this->Route_points_model->create_point();
                }
                $this->db->trans_commit();
                if ($this->db->_error_message()) {
                    $response = array(
                        'saved' => FALSE,
                        'message' => $this->db->_error_message());
                } else {
                    $response = array(
                        'saved' => TRUE, 
                        'id' => $this->Route_model->id
                    );
                }
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

    public function save_tracking() {
        $user = get_user();
        $response = NULL;
        if ($user != NULL) {
            $json = $this->input->post("data");
            $route = json_decode($json);
            $this->load->model("Tracking_model");
            $this->load->model("Tracking_points_model");
            try {
                $this->db->trans_begin();
                $this->Tracking_model->routeid = $route->routeid;
                $this->Tracking_model->userid = $user->id;
                $this->Tracking_model->time = date('Y-m-d H:i:s', $route->time);
                $this->Tracking_model->create_tracking();

                foreach ($route->points as $point) {
                    $this->Tracking_points_model->id = $point->id;
                    $this->Tracking_points_model->trackingid = $this->Tracking_model->id;
                    $this->Tracking_points_model->lat = $point->lat;
                    $this->Tracking_points_model->lon = $point->lon;
                    $this->Tracking_points_model->time = date('Y-m-d H:i:s', $point->time);
                    $this->Tracking_points_model->create_point();
                }
                $this->db->trans_commit();
                if ($this->db->_error_message()) {
                    $response = array(
                        'saved' => FALSE,
                        'message' => $this->db->_error_message());
                } else {
                    $response = array(
                        'saved' => TRUE
                    );
                }
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