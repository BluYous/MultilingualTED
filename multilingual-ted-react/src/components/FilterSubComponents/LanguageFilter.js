import React, {Component} from 'react';
import axios from 'axios';
import {ajaxAddress} from "../../common";
import {message, Select, Spin} from 'antd';
import {PubSub} from 'pubsub-js';

class LanguageFilter extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isLoaded: false,
            languages: [],
        }
    }
    
    handleChange = (value) => {
        PubSub.publish('languageFilter', value);
    };
    
    handleFocus = (value) => {
        let {isLoaded} = this.state;
        if (!isLoaded) {
            axios.get(ajaxAddress + "/languages")
                .then(res => {
                    let languages = res.data;
                    this.setState({
                        isLoaded: true,
                        languages,
                    })
                })
                .catch(e => {
                    message.error('Fail to load languages');
                });
        }
    };
    
    render() {
        let {languages, isLoaded} = this.state;
        return (
            <Select
                showSearch
                allowClear={true}
                style={{width: '100%'}}
                placeholder="Languages"
                optionFilterProp="children"
                notFoundContent={!isLoaded ? <Spin size="default"/> : null}
                onChange={this.handleChange}
                onFocus={this.handleFocus}
            >
                {
                    languages.map((item, index) => {
                        return (
                            <Select.Option
                                key={item.language_code}
                                title={`${item.endonym == null ? '' : item.endonym}  ${item.count} talk(s)`}
                            >
                                {item.language_name}
                            </Select.Option>
                        )
                    })
                }
            </Select>
        )
    }
}

export default LanguageFilter;