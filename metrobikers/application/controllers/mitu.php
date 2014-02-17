<?php

if (!defined('BASEPATH')) {
    exit('No direct script access allowed');
}
        const SUCCESS = 0;
        const CUSTOM_ERROR = 1;
        const USER_NOT_LOGGED = 2;
        const NO_RECEIVER_IDS = 3;

        const MSG_REQUEST_CONTACT = 0;
        const MSG_ACCEPT_CONTACT = 1;
        const MSG_REJECT_CONTACT = 2;
        const MSG_REMOVE_CONTACT = 3;
        const MSG_WRONG_PASSWORD = 4;
        const MSG_MESSAGE = 5;
        const MSG_POSITION = 6;

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

    public function test($userid, $message) {
        if ($this->MITU_User_model->get_user('test')) {
            $old = $this->user;
            $this->user = clone ($this->MITU_User_model);
            if ($this->MITU_User_model->get_user($userid)) {
                $this->internal_message_to_user($this->MITU_User_model->id, MSG_MESSAGE, TRUE, array("message" => $message, "time" => time()));
            }
        }
        $this->user = $old;
    }

    private function send_message($registrationIDs, $data, $collapse_key = NULL) {
        $apiKey = "AIzaSyC2SzSst-NVCnnUKlGegbarNe6SapTgDnk";

        // Set POST variables
        $url = 'https://android.googleapis.com/gcm/send';

        $fields = array(
            'registration_ids' => $registrationIDs,
            'data' => $data
        );
        if ($collapse_key != NULL) {
            $fields['collapse_key'] = $collapse_key;
        }
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

        return $result;
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
                'mail' => $this->user->mail
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
            $this->load->model("MITU_User_position_model");
            $this->load->model("MITU_Connections_model");

            $this->db->trans_begin();

            $this->MITU_User_position_model->userid = $point->id;
            $this->MITU_User_position_model->lat = $point->lat;
            $this->MITU_User_position_model->lon = $point->lon;
            $this->MITU_User_position_model->gps = $point->gps ? 1 : 0;
            $this->MITU_User_position_model->time = date('Y-m-d H:i:s', $point->time);
            $this->MITU_User_position_model->save_position();

            $this->MITU_Connections_model->purge($point->id);
            foreach ($point->users as $userid) {
                $this->MITU_Connections_model->idfrom = $point->id;
                $this->MITU_Connections_model->idto = $userid;
                $this->MITU_Connections_model->create_connection();

                $this->internal_message_to_user($userid, MSG_POSITION, FALSE, array("position" => $point));
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

    public function get_positions() {
        $userid = $this->user == NULL ? 0 : $this->user->id;
        $this->get_positions_by_userid($userid);
    }

    public function get_positions_by_userid($userid) {
        $this->load->model("MITU_User_position_model");
        $this->MITU_User_position_model->purge_positions();
        $response = $this->MITU_User_position_model->get_positions($userid);

        if ($response) {
            foreach ($response as &$point) {
                $point["time"] = strtotime($point["time"]);
                $point["gps"] = $point["gps"] == 1 ? TRUE : FALSE;
            }
        }
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function get_users() {

        $userid = $this->user == NULL ? 0 : $this->user->id;

        $filter = $this->input->get("filter", $userid);

        $response = array(
            'users' => $this->MITU_User_model->get_users($filter, $userid),
            'total' => $this->MITU_User_model->get_user_count($filter, $userid));
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function respond_to_user($id, $response) {
        $this->internal_message_to_user($id, $response);
    }

    public function contact_user() {
        $this->internal_message_to_user($this->input->post("userid"), MSG_REQUEST_CONTACT, TRUE, array("securetoken" => $this->input->post("securetoken")));
    }

    public function disconnect_user($id) {
        $this->internal_message_to_user($id, MSG_REMOVE_CONTACT);
    }

    public function message_to_user() {
        $message = $this->input->post('message');
        $userid = $this->input->post('userid');
        $time = $this->input->post('time');

        $this->internal_message_to_user($userid, MSG_MESSAGE, TRUE, array("message" => $message, "time" => $time));
    }

    private function get_collapse_key($response_code) {
        switch ($response_code) {
            case MSG_REQUEST_CONTACT: return "MSG_REQUEST_CONTACT";
            case MSG_POSITION: return "MSG_POSITION";
            default : return NULL;
        }
    }

    private function internal_message_to_user($id, $response_code, $do_output = TRUE, $args = array()) {
        if ($this->user !== NULL) {
            $this->load->model("MITU_Regid_model");
            $ids = $this->MITU_Regid_model->get_regids($id);
            if (count($ids) === 0) {
                $response->result = NO_RECEIVER_IDS;
            } else {
                $regids = array();
                foreach ($ids as $value) {
                    array_push($regids, $value['regid']);
                }
                $ar = array(
                    'msgtype' => $response_code,
                    'touserid' => $id,
                    'user' => array(
                        'id' => $this->user->id,
                        'userid' => $this->user->userid,
                        'name' => $this->user->name,
                        'surname' => $this->user->surname
                ));
                $ar = array_merge($ar, $args);
                $result = $this->send_message($regids, $ar, $this->get_collapse_key($response_code));
                $response = array('result' => SUCCESS, "gcmresponse" => json_decode($result));
            }
        } else {
            $response = array('result' => USER_NOT_LOGGED);
        }
        if ($do_output) {
            $this->output
                    ->set_content_type('application/json')
                    ->set_output(json_encode($response));
        }
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */
