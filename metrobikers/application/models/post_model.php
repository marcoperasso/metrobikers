<?php

class Post_model extends MY_Model {

    var $userid;
    var $content;
    var $time;

    public function __construct() {
        parent::__construct();
    }

    public function get_post_count($userid) {
        $query = $this->db
                ->select('count(a.id) size')
                ->distinct()
                ->join('users a', 'a.id=b.userid')
                ->join('linkedusers c', 'a.id = c.userid1 or a.id = c.userid2', 'left')
                ->where('(c.userid1=' . $userid . ' or c.userid2= ' . $userid . ' or a.id = ' . $userid . ') and active = 1 ')              
                ->get('posts b');
        $result = $query->row();
        return $result->size;
    }

    public function get_posts($userid, $offset) {
        $query = $this->db
                ->select('content, time, name, surname, nickname')
                ->order_by('time', 'desc')
                ->limit(POST_BLOCK_SIZE, $offset)
                ->distinct()
                ->join('users a', 'a.id=b.userid')
                ->join('linkedusers c', 'a.id = c.userid1 or a.id = c.userid2', 'left')
                ->where('(c.userid1=' . $userid . ' or c.userid2= ' . $userid . ' or a.id = ' . $userid . ') and active = 1 ')              
                ->get('posts b');
        $result = $query->result_array();
        foreach ($result as &$value) {
            $value = (object) $value;
        }
        return $result;
    }

    public function create_post() {
        $this->db->insert('posts', $this);
    }

}