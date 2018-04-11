import React from 'react';
import {Button, Col, Divider, message, Row, Select} from 'antd';
import {ajaxAddress} from "../../common";
import axios from "axios/index";
import qs from "qs";
import SubtitleDetail from "./SubtitleDetail";

export default class Subtitle extends React.Component {
    handleChange = (value) => {
        let {selectedLanguages} = this.state;
        if (value.length > 2) {
            selectedLanguages[1] = value[value.length - 1];
        } else {
            selectedLanguages = value;
        }
        this.setState({selectedLanguages});
        
        if (selectedLanguages.length > 0) {
            this.postData(selectedLanguages);
        } else {
            this.setState({subtitles: [],});
        }
    };
    postData = (selectedLanguages) => {
        axios.post(`${ajaxAddress}/subtitles/${this.props.talkId}`, qs.stringify({
            languages: JSON.stringify(selectedLanguages),
        })).then(res => {
            this.setState({subtitles: res.data});
        }).catch(e => {
            message.error('Fail to load subtitles');
        });
    };
    downloadSubtitles = () => {
        let {selectedLanguages} = this.state;
        if (selectedLanguages.length === 0) {
            message.info('Please input languages to download.');
        } else {
            this.refs.downloadForm.submit();
        }
    };
    
    constructor(props) {
        super(props);
        this.state = {
            subtitleLanguages: [],
            selectedLanguages: [],
            subtitles: [],
        }
    }
    
    componentWillMount() {
        let {talkId, curLanguage} = this.props;
        axios.get(`${ajaxAddress}/subtitleLanguages/${talkId}`)
            .then(res => {
                let subtitleLanguages = res.data;
                let selectedLanguages = [curLanguage];
                this.setState({
                    subtitleLanguages,
                    selectedLanguages,
                });
                this.postData([curLanguage]);
            })
            .catch(e => {
                message.error('Fail to load subtitle languages');
            });
    }
    
    render() {
        let {subtitleLanguages, selectedLanguages, subtitles} = this.state;
        return (
            <div>
                <Row type="flex" justify="center">
                    <Col span={22}>
                        <Select
                            ref="select"
                            mode="multiple"
                            showSearch
                            value={selectedLanguages}
                            allowClear={true}
                            style={{width: '100%'}}
                            placeholder="Subtitle Languages"
                            optionFilterProp="children"
                            onChange={this.handleChange}
                        >
                            {
                                subtitleLanguages.map((item, index) => {
                                    return (
                                        <Select.Option
                                            key={item.language_code}
                                            title={`${item.endonym == null ? '' : item.endonym}${item.is_rtl === 'Y' ? ' right to left' : ''}`}
                                        >
                                            {item.language_name}
                                        </Select.Option>
                                    )
                                })
                            }
                        </Select>
                    </Col>
                    <Col span={1} offset={1}>
                        <form action={`${ajaxAddress}/download/subtitles/${this.props.talkId}`} method='post'
                              style={{display: 'none'}} ref='downloadForm'>
                            <input type="text" name='languages' value={JSON.stringify(selectedLanguages)} readOnly/>
                        </form>
                        <Button type="primary" shape="circle" icon="download" size='default'
                                onClick={this.downloadSubtitles}/>
                    </Col>
                </Row>
                <Divider/>
                <SubtitleDetail subtitles={subtitles}/>
            </div>
        )
    }
}