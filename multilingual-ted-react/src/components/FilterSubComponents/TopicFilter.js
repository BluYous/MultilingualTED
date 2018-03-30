import React, {Component} from 'react';
import axios from 'axios';
import {ajaxAddress} from "../../common";
import {Row, Col} from 'antd';
import {Menu, Dropdown, Icon} from 'antd';

class TopicFilter extends Component {
    constructor(props) {
        super(props);
        this.state = {
            topicFilter: null,
            topTopics: [],
        }
    }
    
    componentWillMount() {
        axios.get(ajaxAddress + "/api/topTopics")
            .then(res => {
                let topTopics = res.data;
                this.setState({
                    topTopics
                })
            })
            .catch(e => {
                console.log(e);
            });
    }
    
    render() {
        let {topTopics} = this.state;
        console.log("top" + topTopics);
        const menu = (
            <Menu>
                {
                    topTopics.map((item, index) => {
                        return (
                            <Menu.Item key={item.topic_id}>
                                <a href="#">{item.label}</a>
                            </Menu.Item>
                        )
                    })
                }
                <Menu.Divider/>
                <Menu.Item key="3">See all topics</Menu.Item>
            </Menu>
        );
        return (
            <Dropdown overlay={menu} trigger={['click']}>
                <a href="#">
                    Topics <Icon type="down"/>
                </a>
            </Dropdown>
        )
    }
}

export default TopicFilter;