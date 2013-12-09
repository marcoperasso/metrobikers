<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Home extends MY_Controller {

    public function __construct() {
        parent::__construct();
    }

    public function index() {
        if ($this->user == NULL)
            $this->load_view('home_not_logged', "Benvenuto in ECOmmuters");
        else {
            $this->load->model('Post_model');
            $data = array(
                'posts' => $this->Post_model->get_posts($this->user->id, 0),
                'count' => $this->Post_model->get_post_count($this->user->id)
            );
            $this->load_view('home_logged', "Benvenuto in ECOmmuters", $data);
        }
    }

    public function get_more_posts($offset) {
        $this->load->model('Post_model');
        $data = array(
            'posts' => $this->Post_model->get_posts($this->user->id, $offset),
            'user' => $this->user);

        $this->load->view('posts', $data);
    }

    public function post() {
        if (!$this->validate_login())
            return;
        $this->load->model('Post_model');
        $this->Post_model->userid = $this->user->id;
        $this->Post_model->content = $this->input->post('content');
        $this->Post_model->create_post();
        $this->index();
    }

    public function update_post() {
        if (!$this->validate_login())
            return;
        $this->load->model('Post_model');
        $this->Post_model->userid = $this->user->id;
        $this->Post_model->content = $this->input->post('postcontent');
        $this->Post_model->time = $this->input->post('posttime');
        
        $response =(object) array('result' => $this->Post_model->update_post());
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

    public function delete_post() {
        if (!$this->validate_login())
            return;
        $this->load->model('Post_model');
        $this->Post_model->userid = $this->user->id;
        $this->Post_model->time = $this->input->post('posttime');
        
        $response =(object) array('result' => $this->Post_model->delete_post());
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }
    public function mission() {
        $this->load_view('mission');
    }

    public function details() {
        $this->load_view('details');
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */