<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Mitu extends CI_Controller {

    var $user;

    public function __construct() {
        parent::__construct();
        if (!isset($_SESSION)) {
            session_start();
        }
        $this->lang->load("messages", "it-IT");
        $this->load->model('MITU_User_model');

        $this->user = (isset($_SESSION) && isset($_SESSION['user'])) ? unserialize($_SESSION["user"]) : NULL;
    }
    
    public function send_message($message) {
        $apiKey = "AIzaSyC2SzSst-NVCnnUKlGegbarNe6SapTgDnk";

        // Replace with real client registration IDs 
        $registrationIDs = array("APA91bGwEzb3tq9CWAH2X_l8601VED4P7tkn9W_RNrKPfR8d0JEyX2b89mFQNa6tu7c5dKZYq5W5E10zrQ_UJhKnZiXCzCYvmVmJlG9SNWvr_KtC1S-5Fm7KzW_JknjQhGjA3fYeQiNsCNX2rQivZRnqMTzby_ml9Q");

        // Set POST variables
        $url = 'https://android.googleapis.com/gcm/send';

        $fields = array(
            'registration_ids' => $registrationIDs,
            'data' => array("message" => $message),
        );

        $headers = array(
            'Authorization: key=' . $apiKey,
            'Content-Type: application/json'
        );

        // Open connection
        $ch = curl_init();

        // Set the url, number of POST vars, POST data
        curl_setopt($ch, CURLOPT_URL, $url);

        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

        // Execute post
        $result = curl_exec($ch);

        // Close connection
        curl_close($ch);

        echo $result;
    }

    public function save_regid() {
        $response = array('success' => FALSE);
        if ($this->user !== NULL) {
            $this->load->model("MITU_Regid_model");
            $this->MITU_Regid_model->userid = $this->user->id;
            $this->MITU_Regid_model->regid = $this->input->post('regid');
            $response['success'] = $this->MITU_Regid_model->create_regid();
        }

        $this->output->set_content_type('application/json')->set_output(json_encode($response));
    }

    public function save_user() {
        $userid = $this->input->post('userid');
        $newuser = $this->input->post('newuser');
        $existing = $this->MITU_User_model->get_user($userid);
        $response = array('success' => TRUE);
        if ($existing) {
            if ($newuser) {
                $response["message"] = "User already existing";
                $response["success"] = FALSE;
                $this->output->set_content_type('application/json')->set_output(json_encode($response));
                return;
            }
        }
        $this->MITU_User_model->mail = $this->input->post('email');
        $this->MITU_User_model->userid = $userid;

        $this->load->library('BCrypt');
        $bcrypt = new BCrypt(15);

        $this->MITU_User_model->password = $bcrypt->hash($this->input->post('pwd'));
        $this->MITU_User_model->name = $this->input->post('name');
        $this->MITU_User_model->surname = $this->input->post('surname');

        if ($existing) {
            $this->MITU_User_model->update_user();
        } else {
            $this->MITU_User_model->create_user();
            $response["id"] = $this->MITU_User_model->id;
        }
        $this->output->set_content_type('application/json')->set_output(json_encode($response));
    }

    public function login() {
        $pwd = $this->input->post('pwd');
        $userid = $this->input->post('userid');
        
        $this->load->library('BCrypt');
        $bcrypt = new BCrypt(15);

        $success = $this->MITU_User_model->get_user($userid) && $bcrypt->verify($pwd, $this->MITU_User_model->password);
        $response = array('success' => $success, 'version' => 1);
        if ($success) {
            set_user($this->MITU_User_model);
        } else {
            $response["message"] = "Login failed. Invalid user or password";
        }

        $this->output->set_content_type('application/json')->set_output(json_encode($response));
    }

    public function user_logged() {
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode(array('logged' => $this->user !== NULL)));
    }

    public function user() {
        $response = NULL;
        if ($this->user != NULL) {
            $response = array(
                'logged' => TRUE,
                'id' => $this->user->id,
                'userid' => $this->user->userid,
                'name' => $this->user->name,
                'surname' => $this->user->surname,
                'mail' => $this->user->mail,
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

    public function update_position() {

        $json = $this->input->post("data");
        $point = json_decode($json);
        if ($point) {
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
        } else {
            $response = array('saved' => FALSE);
        }
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function get_positions($left, $top, $right, $bottom) {
        $this->load->model("User_position_model");
        $this->load->model("User_on_route_model");
        $this->User_position_model->purge_positions();
        $search_userid = $this->input->post("userid");
        if (!$search_userid)
            $search_userid = 0;
        $userid = $this->user == NULL ? 0 : $this->user->id;
        $response = $this->User_position_model->get_positions($left, $top, $right, $bottom, $userid, $search_userid);

        if ($response) {
            foreach ($response as &$point) {
                $point["time"] = strtotime($point["time"]);
                $point["routes"] = $this->User_on_route_model->get_routes($point["userid"]);
            }
        }
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function get_positions_by_name() {
        $name = $this->input->get("name");
        $this->load->model("User_position_model");
        $this->load->model("User_on_route_model");
        $this->User_position_model->purge_positions();
        $list = $this->User_position_model->get_positions_by_name($name);

        foreach ($list as &$point) {
            $point["time"] = strtotime($point["time"]);
            $point["routes"] = $this->User_on_route_model->get_routes($point["userid"]);
        }
        $response = array('positions' => $list, 'total' => $this->User_position_model->get_positions_count_by_name($name));
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */
