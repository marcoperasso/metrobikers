<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Mitu extends MY_Controller {

    public function save_user() {
        $this->load->model("MITU_User_model");
		$userid = $this->input->post('userid');
		$newuser = $this->input->post('newuser');
		$existing = $this->MITU_User_model->get_user($userid);
		$response = array('success' => TRUE);
		if ($existing)
		{
			if ($newuser)
			{
				$response["message"] = "User already existing";
				$response["success"] = FALSE;
				$this->output->set_output(json_encode($response));
			}
		}
        $this->MITU_User_model->mail = $this->input->post('email');
		$this->MITU_User_model->userid = $userid;
		
		$this->load->library('BCrypt');
        $bcrypt = new BCrypt(15);
		
        $this->MITU_User_model->password = $bcrypt->hash($this->input->post('pwd'));
        $this->MITU_User_model->name = $this->input->post('name');
		$this->MITU_User_model->surname = $this->input->post('surname');
		
		if ($existing)
		{
			$this->MITU_User_model->update_user();
		}
		else
		{
			$this->MITU_User_model->create_user();
			$response["id"] = $this->MITU_User_model->id;
		}
		$this->output->set_output(json_encode($response));
    }

    public function login() {
        $pwd = $this->input->post('pwd');
        $userid = $this->input->post('userid');
        $this->load->model('MITU_User_model');

        $this->output->set_content_type('application/json');
        $this->load->library('BCrypt');
        $bcrypt = new BCrypt(15);
        
        $success = $this->MITU_User_model->get_user($userid) && $bcrypt->verify($pwd, $this->MITU_User_model->password);
        $response = array('success' => $success, 'version' => 1);
        if ($success) {
            set_user($this->MITU_User_model);
        } else {
            $response["message"] = "Login failed. Invalid user or password";
        }

        $this->output->set_output(json_encode($response));
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