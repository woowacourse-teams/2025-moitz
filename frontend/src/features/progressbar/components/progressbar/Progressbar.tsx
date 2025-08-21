import * as styles from './Progressbar.styled';

interface ProgressbarProps {
  progress: number;
}

function Progressbar({ progress }: ProgressbarProps) {
  return (
    <div css={styles.container()}>
      <div css={styles.bar(progress)} />
    </div>
  );
}

export default Progressbar;
