import React, {Component} from 'react';
import axios from 'axios';
import {ajaxAddress} from "../../common";
import {message, Select, Spin} from 'antd';
import {PubSub} from 'pubsub-js';

class TopicFilter extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isLoaded: false,
            topics: [],
        }
    }
    
    handleChange = (value) => {
        PubSub.publish('topicsFilter', value);
    };
    
    handleFocus = (value) => {
        let {isLoaded} = this.state;
        if (!isLoaded) {
            axios.get(`${ajaxAddress}/topics`)
                .then(res => {
                    let topics = res.data;
                    this.setState({
                        isLoaded: true,
                        topics,
                    })
                })
                .catch(e => {
                    message.error('Fail to load topics');
                });
        }
    };
    
    render() {
        let {topics, isLoaded} = this.state;
        return (
            <Select
                allowClear={true}
                mode="multiple"
                style={{width: '100%'}}
                placeholder="Topics"
                notFoundContent={!isLoaded ? <Spin size="default"/> : null}
                onChange={this.handleChange}
                onFocus={this.handleFocus}
                maxTagCount={3}
            >
                {
                    topics.map((item, index) => {
                        return (
                            <Select.Option key={item.topic_id} title={`${item.count} talk(s)`}>
                                {item.label}
                            </Select.Option>
                        )
                    })
                }
            </Select>
        )
    }
}

export default TopicFilter;