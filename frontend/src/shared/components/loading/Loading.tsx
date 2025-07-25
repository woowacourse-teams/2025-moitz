/** @jsxImportSource @emotion/react */
import { motion } from 'framer-motion';

import { flex } from '@shared/styles/default.styled';

import IconLogo from '@icons/logo-icon.svg';
import IconLogoTextWhite from '@icons/logo-text-white.svg';

import * as loading from './loading.styled';

function Loading() {
  return (
    <div
      css={[
        loading.container,
        flex({ justify: 'center', align: 'center', gap: 5 }),
      ]}
    >
      <div css={loading.content}>
        <motion.img
          src={IconLogo}
          alt="loading"
          animate={{ rotate: 360 }}
          transition={{
            duration: 0.8,
            repeat: Infinity,
            ease: 'linear',
            repeatDelay: 0.5,
          }}
        />
      </div>
      <img src={IconLogoTextWhite} alt="logo-text-white" />
    </div>
  );
}

export default Loading;
