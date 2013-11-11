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

    public function get_route_for_user($userid, $routeid) {
        $this->load->model("Route_model");
        $this->Route_model->id = $routeid;
        $this->Route_model->userid = $userid;
        $response = NULL;
        if ($this->Route_model->get_route_by_id()) {
            $this->Route_model->points = $this->Route_model->get_points();
            $response = $this->Route_model;
        }
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function get_routes($latestupdate) {
        $user = get_user();
        if ($user != NULL) {
            $this->load->model("Route_model");
            $this->Route_model->userid = $user->id;
            $response = $this->Route_model->get_routes(date('Y-m-d H:i:s', $latestupdate));
            foreach ($response as $route) {
                $route->points = $route->get_points();
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
        $this->load->model("User_on_route_model");
        $this->db->trans_begin();

        $this->User_position_model->userid = $point->userid;
        $this->User_position_model->lat = $point->lat;
        $this->User_position_model->lon = $point->lon;
        $this->User_position_model->time = date('Y-m-d H:i:s', $point->time);
        $this->User_position_model->save_position();

        $this->User_on_route_model->purge($point->userid);
        foreach ($point->routes as $routeid) {
            $this->User_on_route_model->userid = $point->userid;
            $this->User_on_route_model->routeid = $routeid;
            $this->User_on_route_model->save();
        }
        $this->db->trans_commit();
        $response = array('saved' => TRUE);
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function get_positions($left, $top, $right, $bottom) {
        $this->load->model("User_position_model");
        $this->load->model("User_on_route_model");
        $this->User_position_model->purge_positions();
        $response = $this->User_position_model->get_positions($left, $top, $right, $bottom);

        foreach ($response as &$point) {
            $point["time"] = strtotime($point["time"]);
            $point["routes"] = $this->User_on_route_model->get_routes($point["userid"]);
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
            try {
                $this->Route_model->name = $route->name;
                $this->Route_model->userid = $user->id;
                $this->db->trans_begin();
                
                if (!$this->Route_model->get_route()) {
                    $this->Route_model->latestupdate = date('Y-m-d H:i:s', $route->latestupdate);
                    $this->Route_model->set_points($route->points);
                    $this->Route_model->create_route();
                } else {
                    $latestupd = max(array($route->latestupdate, $this->Route_model->latestupdate));
                    $this->Route_model->latestupdate = date('Y-m-d H:i:s', $latestupd);
                    $this->Route_model->set_points($route->points);
                    $this->Route_model->update_route();
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
            try {
                $this->db->trans_begin();
                $this->Tracking_model->routeid = $route->routeid;
                $this->Tracking_model->userid = $user->id;
                $this->Tracking_model->start = date('Y-m-d H:i:s', $route->start);
                $this->Tracking_model->end = date('Y-m-d H:i:s', $route->end);
                $this->Tracking_model->create_tracking();
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